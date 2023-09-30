// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.spark.spanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.spanner.BatchReadOnlyTransaction;
import com.google.cloud.spanner.BatchTransactionId;
import com.google.cloud.spanner.Partition;
import com.google.cloud.spanner.ResultSet;
import java.io.IOException;
import java.util.Map;
import org.apache.spark.sql.catalyst.InternalRow;

public class SpannerInputPartitionReaderContext
    implements AutoCloseable, InputPartitionReaderContext<InternalRow> {

  private BatchClientWithCloser batchClientWithCloser;
  private ResultSet rs;

  public SpannerInputPartitionReaderContext(
      Partition partition, BatchTransactionId batchTransactionId, String mapAsJSONStr) {
    Map<String, String> opts;
    try {
      opts = SpannerUtils.deserializeMap(mapAsJSONStr);
    } catch (JsonProcessingException e) {
      throw new SpannerConnectorException(
          SpannerErrorCode.SPANNER_FAILED_TO_PARSE_OPTIONS, "Error parsing the input options.", e);
    }

    // Please note that we are using BatchClientWithCloser to avoid resource leaks.
    // That is because, since we do have a deterministic scope and timeline for how long
    // SpannerInputPartitionReaderContext's BatchClient.Spanner will execute, we use this
    // custom client with an AutoCloser that'll clean up resources as it is garbage collected.
    this.batchClientWithCloser = SpannerUtils.batchClientFromProperties(opts);
    try (BatchReadOnlyTransaction txn =
        batchClientWithCloser.batchClient.batchReadOnlyTransaction(batchTransactionId)) {
      this.rs = txn.execute(partition);
    }
  }

  @Override
  public boolean next() throws IOException {
    return this.rs.next();
  }

  @Override
  public InternalRow get() {
    return SpannerUtils.resultSetRowToInternalRow(this.rs);
  }

  @Override
  public void close() throws IOException {
    if (this.rs != null) {
      this.rs.close();
      this.rs = null;
    }
    if (this.batchClientWithCloser != null) {
      this.batchClientWithCloser.close();
      this.batchClientWithCloser = null;
    }
  }
}

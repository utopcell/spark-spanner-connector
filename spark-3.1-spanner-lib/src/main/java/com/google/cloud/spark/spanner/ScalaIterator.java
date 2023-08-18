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

/*
 * Copied from spark-bigquery-connector-common
 */
import java.util.Iterator;
import scala.collection.AbstractIterator;

// Scala version agnostic conversion
class ScalaIterator<T> extends AbstractIterator<T> {

  private Iterator<T> underlying;

  public ScalaIterator(Iterator<T> underlying) {
    this.underlying = underlying;
  }

  @Override
  public boolean hasNext() {
    return underlying.hasNext();
  }

  @Override
  public T next() {
    return underlying.next();
  }
}

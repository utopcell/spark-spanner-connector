DELETE FROM ATable WHERE 1=1;

INSERT INTO
    ATable(A, B, C, D, E)
VALUES
    (1,  "2",  NULL, TIMESTAMP("2023-08-22 12:22:00+00"), 1000.282),
    (10, "20", NULL, TIMESTAMP("2023-08-22 12:23:00+00"), 10000.282),
    (30, "30", NULL, TIMESTAMP("2023-08-22 12:24:00+00"), 30000.282);

DELETE FROM simpleTable WHERE 1=1;

INSERT INTO
    simpleTable(A, B, C)
VALUES
    (1, "1", 2.5),
    (2, "2", 5.0);
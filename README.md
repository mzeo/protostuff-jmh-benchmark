Used for benchmarking patch to protostuff with packed field support.

```
# Unpatched code, not packed
Benchmark                                Mode  Samples      Score     Error  Units
s.m.p.ProtostuffBenchmark.testMethod    thrpt      200  90236.485 ± 904.257  ops/s

# Unpatched code, packed
io.protostuff.ProtobufException: Protocol message contained an invalid tag (zero).

# Patched code, not packed
Benchmark                                Mode  Samples      Score     Error  Units
s.m.p.ProtostuffBenchmark.testMethod    thrpt      200  87870.775 ± 430.595  ops/s

# Patched code, packed
Benchmark                                Mode  Samples      Score     Error  Units
s.m.p.ProtostuffBenchmark.testMethod    thrpt      200  93084.164 ± 995.681  ops/s
```


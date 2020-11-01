# data-warehouse
Simple Data Warehouse (extract, transform, load, query)

### Build project
```
./gradlew build
```

### Build Docker file
```
$ docker build -t al3x3i/warehouse .
$ docker run -p 8080:8080 al3x3i/warehouse
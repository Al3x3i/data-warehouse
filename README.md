# data-warehouse
Simple Data Warehouse (extract, transform, load, query)

### Build project
```
./gradlew build
```

### Run 
```
$ ./gradlew bootrun
```
After running the application, the database will be empty.

Please upload CSV (is attached to this project `PIxSyyrIKFORrCXfMYqZBI.csv`) before using API end-points 

### Generate API documentation
```
$ ./gradlew asciidoctor
```

### API documentation
```
$ cd build/asciidoc/html5
```

### Build Docker file
```
$ docker build -t al3x3i/warehouse .
$ docker run -p 8080:8080 al3x3i/warehouse

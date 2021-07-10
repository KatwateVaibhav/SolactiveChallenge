### How to run tests and application locally

  To run all tests from command line, just use the following command.
  
```
mvn test
```
	To start the application, run the following command, assuming maven and jdk8+ are installed.
    
```
mvn spring-boot:run 
```

## End points:

### 1. For Adding Ticks:
```
curl -X POST -H "Content-Type: application/json" -d "{ \"instrument\": \"IBM.N\" ,\"price\": 100.0 , \"timestamp\" : 2579258809302}" http://localhost:9020/ticks/
```

### 2. Getting Ticks of specific Instrument:

```
curl --request GET http://localhost:9020/statistics/IBM.N
```
### 3. Getting Ticks of ALL Instrument:
  
```
curl --request GET http://localhost:9020/statistics
```


## Exceptions thrown
* Exception handling is done, using a framework. @ExceptionHandler deals with  all the exceptions thrown from application.

Handled Scenarios 
`TransactionTimestampException,
`InvalidInstrumentException,

* Unit tests have been used for all the layers.


# Things that can be improved:
* The logging can be improved 
* The Exception handling can be added more, by more testing the edge cases. 
* A Distributed cache could be used instead of In Memory. That will add to the performance for sure.



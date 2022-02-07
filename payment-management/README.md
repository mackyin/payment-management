# Read Me First


# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#production-ready)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

#### Building and Testing
  `mvn clean install`
  
  Added Test cases for all the rest-endpoints & some business logics 
  TO DO : can add more test cases to mock the logics
  Refer ValidateBusinessFlowTest.java end to end business validation

#### Run the application

  `mvn spring-boot:run`
  
#### Swagger UI can be access via
* [Swagger](http://localhost:8081/swagger-ui/index.html#/)

For All the Post API,remove the id attribute from request body before post the request . with id will be consider as edit & currently edit not supported it will throw bad request error

#### H2 DB console UI can be access via
*[H2 DB console UI](http://localhost:8081/h2-console/login.jsp)
 user name : sa
 password : password
 
#### Domain Model design
Customer ---> Order (M:1)

Order ----> Payment (M:1)

#### Assumptions :
   Customer entity contains the customerBalance & walletbalance ,customerBalance indicated net balance to pay for all orders &  walletbalance shows how much          additional money paid by customer 
   
  Order entity contains net amount for each order & Current status of orders (UNDER_PAY orders consider as PRNDING orders,FULLY_PAID or OVER_PAID orders consider   as COMPLETED orders)
  
  Payment entity will track all they payment made by user & PaymentDTO have flag to make to true or false to reduct money from wallet.in UI perspective user will   be able to decide how to proceed the payment,if user select useWalletBalance then one payment entry will be create in Payment table on behalf of ZOOPLUS_WALLET.
  Currently by default useWalletBalance is true always
  
  TO DO: need to keep one more transaction status in Payment table to ensure 3rd party payments   



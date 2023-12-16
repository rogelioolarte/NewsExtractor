
# NewsExtractor v 1.0.0

API dedicated to extract news  using CSS query selectors documented with OpenAPI 3.

## About

The API uses a pattern using [CSS query selectors](https://jsoup.org/cookbook/extracting-data/selector-syntax) to extract news using a maximum of three phases, which can be reduced as needed.

This API used the following dependencies:

* [SpringBoot - Maven](https://spring.io/)
    - Lombox 
    - Validation
    - Spring Data JPA
    - Spring Web
    - MySQL Driver
* [SpringDoc-OpenAPI](https://springdoc.org/)
* [Jsoup](https://jsoup.org/)

## Pre-requisites
* [Java JDK v17.0.9](https://www.oracle.com/java/technologies/downloads/#java17)
* [Maven 3](https://maven.apache.org/download.cgi)

## Usage

* Important: Check the ApplicationConfig.java, because the database-config use environment variables.

### Install the prerequisites, then in the folder of the project: 
    - mvn clean install
    - java -cp target/NewsExtractor.jar

Next, you check [This endpoint](http://localhost:8080/swagger-ui/index.html) 


## Features:
    - You can use a add specific sections.
    - You can use a add specific articles or article sources.
    - You can search for words in common in all newspaper articles.
    - If the page to be extracted does not have connection problems,
      it is possible to extract and save up to 0.80 seconds per article.

## Note:
Minimal use requires a newspaper source and a pattern with at least one specific selector.

## FAQ:

- Does the API work completely?
Due to the fact that tests have not yet been integrated, some pages without the correct adjustment, it will not be possible to completely extract all the news for now because some pages limit requests or have sources that take a long time to return a response.

- If you have a suggestion or advice, feel free to send me an email.

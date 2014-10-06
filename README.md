#Twitter Location Parser

Fast twitter user's location parser

This project purpose is to parse the location field in twitter's [user](https://dev.twitter.com/docs/platform-objects/users) object.

The main requirment while writing this project is to support the speed of [Twitter's firehose streaming api](https://dev.twitter.com/docs/api/1.1/get/statuses/firehose), which is ~10000 msg / sec.

###Getting Started
####_Installation_
#####_Maven_
_*Currntly the project is not deplyed in [maven](http://maven.apache.org/) repoistory._

Download the latest version [here](https://github.com/taykey/twitter-location-parser/archive/twitter-location-parser-0.9.1.tar.gz) and run `mvn install` to install it into your local maven repository.
Then include this dependency in the pom.xml

    <dependency>
      <groupId>com.taykey</groupId>
      <artifactId>twitter-location-parser</artifactId>
      <version>${twitter-location-parser.version}</version>
    </dependency>   


Another option is to run `mvn package` and use the jar in `target` folder.

####_Usage_

    import com.taykey.twitterlocationparser.dto.Location
    import com.taykey.twitterlocationparser.LocationParser;
    import com.taykey.twitterlocationparser.DefaultLocationParser;
    .
    .
    .
    LocationParser locationParser = new DefaultLocationParser();
    String text = "i live in Tel Aviv";
    Location location = locationParser.parseText(text);
    System.out.println(location.getName());


Or by using [Spring](http://spring.io/):

Import the spring config of twitter location parser by adding this to the spring-config.xml:

    <import resource="classpath:twitter-location-parser-spring-config.xml"/>

And then:

    import com.taykey.twitterlocationparser.dto.Location
    import com.taykey.twitterlocationparser.LocationParser;
    .
    .
    .
    @Autowired
	  private LocationParser locationParser;
    .
    .
    .
    String text = "i live in Tel Aviv"
    Location location = locationParser.parseText(text)
    System.out.println(location.getName());
    

####TODO's:
The To-do's for the project are location in [TODO](https://github.com/taykey/twitter-location-parser/blob/master/TODO) file.

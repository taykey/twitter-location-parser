twitter-location-parser
=======================

Twitter user's location parser

This project purpose is to parse the location field in [twitter's user](https://dev.twitter.com/docs/platform-objects/users)  location field.

The main requirment while writing this project is to support the speed of [Twitter's firehose streaming api](https://dev.twitter.com/docs/api/1.1/get/statuses/firehose), that is ~10000 msg / sec.

# Ad Server

Micro service to add ad data and zone data, and to forecast rate of impressions for all adds in a given zone. 

The rate of impressions for each ad in a given set of ads in a zone is calculated by:


1. by keeping track of the total daily ad impressions allowed for the given zone
2. for each ad:
  1. calculating the number of days that the ad runs using it's start and end date.
  2. calculating the number of impressions per day the add can run to achieve it's goal impression for the number of days the ad will run.
  3. if the total daily ad impressions is greater than zero, and less than the number of daily ad impressions for an ad, the total daily ad impression are divided by the number of daily ad impressions for an ad to get the impression rate for that ad.
  4. if the total daily ad impressions is greater than the number of daily ad impressions for an ad, the daily ad impressions for the ad is subtracted from the total daily ad impression for a new remaining total daily ad impression for the zone and the ad impression rate is set to 100%.
  5. if the total daily ad impressions is 0 then the ad impression rate is set to 0%.


## Install

```
Compile or download the adserver.jar.
cd to the directory where the adserver.jar is.
On the command line type: 'java -jar adserver.jar'

Requires Java 1.7 JDK or higher.
```

## Usage

**Add Ad Data**

To add ad data supply the following URL with a JSON object containing the ad data to add. 

curl -H "Content-Type: application/json" -d "<JSON ad object, see below>" -X POST http://localhost:8080/adserver/services/add

```
{ 
	id: number, 
	zone: number, 
	start: 'YYYY-MM-DD', 
	end: 'YYYY-MM-DD', 
	creative: string,
	priority: number (greater than -1), 
	goal: number (greater than 0) 
}
```

**Add Zone Data**

To add zone data supply the following URL with a JSON object containing zone data to add.

curl -H "Content-Type: application/json" -d "<JSON zone object, see below>" -X POST http://localhost:8080/adserver/services/add/zone

```
{ 
	id: number, 
	title: string, 
	impressions: number (greater than 1) 
}
```

**Bulk Adding**

The micro service supports adding multiple zones and ad by encapulating the JSON object in a JSON array and encapulating that array in a JSON object such as:

```
{ 
	items: [{}, {}, {}...]
}
```

**Forecast** 

The micro service supports calculating impression forecasts for a specific zone using the following URL.

http://localhost:8080/adserver/services/forecast/<ZONE_ID>?toDate="YYYY-MM-DD"

Note: "toDate is optional", if omitted today's date will be used.

```
{
	"items":[
		{"adId":111,"impressionRate":1,"adZoneId":123},
		{"adId":333,"impressionRate":1,"adZoneId":123},
		{"adId":222,"impressionRate":0.375,"adZoneId":123},
		{"adId":444,"impressionRate":0,"adZoneId":123}
	]
}
```

**Error JSON**

```
{
	"code":number,
	"error":string (Error Name),
	"message":string
}
```

**Error Codes**

| Error         | Code          | Message                                              |
| ------------- |:-------------:| ----------------------------------------------------:|
| SYSTEM_ERROR                  | 9999   | Plain text message.                         |
| ADDATA_NOT_FOUND_FOR_ZONE     | 1000   | Ad zone id for which ad data not found.     |
| ADZONE_DATA_NOT_FOUND         | 1001   | Ad zone id for which zone data not found.   |
| MISSING_REQUIRED_DATA         | 1002   | Field name missing.                         |
| ADDATA_START_END_DATE_ERROR   | 1003   | Plain text message.                         |
| DATA_LESS_THAN_ONE            | 1004   | Field name with incorrect value.            |
| INVALID_DATE_FORMAT           | 1005   | None                                        |
| DATA_LESS_THAN_ZERO           | 1006   | Field name with incorrect value.            |


## Release Notes

2018-04-02 (0.1.000):

* Initial release.

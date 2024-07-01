# Task

It is necessary to implement a Java class (using version 17 if preferred) for interacting with the 'Честный знак' (Honest Mark) API. The class should be thread-safe and support limiting the number of API requests within a specified time interval. This limit is set in the constructor as the maximum number of requests within that interval. For example:

```java
public CrptApi(TimeUnit timeUnit, int requestLimit)
```

Where:

- `timeUnit` specifies the time interval (e.g., seconds, minutes).
- `requestLimit` is a positive value determining the maximum number of requests allowed within that time interval.

When the limit is exceeded, subsequent API calls should be blocked to prevent surpassing the maximum number of requests. Execution should continue without throwing an exception if the API call would not exceed the limit.

Only one method needs to be implemented:

- Creating a document for introducing goods into circulation produced in Russia.

The document and its signature should be passed to the method as a Java object and a corresponding string, respectively.

The method is called via HTTPS POST at the following URL:

```text
https://ismp.crpt.ru/api/v3/lk/documents/create
```

The request body should be in JSON format, structured as follows:

```json
{
  "description": {
    "participantInn": "string"
  },
  "doc_id": "string",
  "doc_status": "string",
  "doc_type": "LP_INTRODUCE_GOODS",
  "importRequest": true,
  "owner_inn": "string",
  "participant_inn": "string",
  "producer_inn": "string",
  "production_date": "2020-01-23",
  "production_type": "string",
  "products": [
    {
      "certificate_document": "string",
      "certificate_document_date": "2020-01-23",
      "certificate_document_number": "string",
      "owner_inn": "string",
      "producer_inn": "string",
      "production_date": "2020-01-23",
      "tnved_code": "string",
      "uit_code": "string",
      "uitu_code": "string"
    }
  ],
  "reg_date": "2020-01-23",
  "reg_number": "string"
}
```

For implementation, you may use HTTP client libraries and JSON serialization. The implementation should be designed for easy future expansion of functionality.

The solution should be structured as a single file named `CrptApi.java`. All additional classes used should be inner classes.

You can provide a link to the file on GitHub. The task is to simply make a call to the specified method; actual API functionality is not required to be implemented.

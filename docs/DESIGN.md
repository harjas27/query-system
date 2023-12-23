# Design
![alt text](https://github.com/harjas27/query-system/blob/main/docs/design.jpg?raw=true)

# Components

## Tenant Service

### Storage
* Each tenant represented by a UUID
* Metadata stored in a sql db

### Onboarding
* Create an ADMIN user
* Admin adds others


## Token Service

### Creation
* System token 
* User Tokens - created using the system token for users (ADMIN can add others)
* Run As Tokens - tokens with short expiry period for services
* SSO support
* Store tokens - nosql db - flexible schema - write heavy

### Authentication
* authenticate using the system token
* generate auth info (tenant, user, claims)


## Integration Service

### Adding an integration
* Connection details
* Registering this application with the enterprise app
* Sharing public key / cert and generating token (credentials)
* Only ADMIN

### Storage
* Store the token in Vault - (id, secret)
* Store the connection details in postgres - (id, name, application_kind, connection_config_json)


## Model Store
* SQL db
* Manually updated
* for each application, stores the generic naming for fields
* alias - as referred in each of the application and custom (User defined)

### Tables
* Objects (Account, Contact ...) - (name*, generic_name, application_kind*)
* Fields (name, email ...) - (name*, generic_name, object_name*, application_kind*)
* Implement a `caching client` to avoid requests

## Querying Service

### Executor
* Get Applications involved, given the fields in the query
* Get integrated applications from `Integration Service`

* Convert the generic fields into app specific ones (use Model Store)
* Fetch connector (enterprise app) details
* Get the connector auth credentials from `Integration Service (vault)`, fetch oauth token from the application (pass user claims)

* Perform Queries by calling the REST endpoints
* Merge the results

* Expoenential Backoff retries for `Code 429` or `X-RateLimit-Limit` header

* Query Result addons:
	* removing duplicates
	* projection config for fields - `first_name + last_name`

### Separate Execution Layer
* For cases where the enterprise app cannot expose an endpoint
* VPC / On Premise deployment

### HPA (Kubernetes Deployment)
* Executor publish metrics for the queries (count per second, average time taken, latency)
* Scale the pods using rules

### Long Running Queries
* Publish result to a messaging queue
* User Defined
* Rule Based: 5 or more queries with no filter
* Defining scope: 
	* Get Cardinaltity of fields periodically 
	* Use `Model Store`


# Query Examples
* All Accounts with employee count > 1000
``` 
{
  "object": "Account",
  "fields": [
    "owner",
    "name",
    "description"
  ],
  "filter": {
    "filterExp": {
      "field": "number_of_employees",
      "operator": "GT",
      "value": 1000
    }
  }
}
```

* All Contacts with the first name Alice AND a last name other than Smith, OR contacts that don't have a value for the property email
```
{
  "object": "Contacts",
  "fields": [
    "first_name",
    "email_addresses",
    "phone_numbers",
    "last_activity_at"
  ],
  "filter": {
    "expTree": {
      "operator": "OR",
      "nodes": [
        {
          "filterExp": {
            "field": "email",
            "operator": "NOT_HAS_PROPERTY"
          }
        },
        {
          "expTree": {
            "operator": "AND",
            "nodes": [
              {
                "filterExp": {
                  "field": "firstname",
                  "operator": "EQ",
                  "value": "Alice"
                }
              },
              {
                "filterExp": {
                  "field": "lastname",
                  "operator": "NEQ",
                  "value": "Smith"
                }
              }
            ]
          }
        }
      ]
    }
  }
}
```

# Store Service Registry

This service offers frontend services a list of all store services available in the backend.

Documentation will follow.

# Read from persisted cache

If a json-encoded string is in /usr/src/store-service-registry/cache/storeServiceInfo.json it will seed the registry:

```json
{"dummy":{"UID":"dummy","name":"dummyStore","provider":"dummyProvider","frontendURL":"http://dummy.de","timestamp":1234}}
```

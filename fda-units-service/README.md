# Units Service

Suggest and validates units of measurements defined in Ontology of units of Measure (OM) [1] is used. 

[1] https://github.com/HajoRijgersberg/OM

## `POST /api/units/suggest`
Autosuggests user typed in terms. 

Example http request: 
POST /api/units/suggest HTTP/1.1
Content-Type: application/json
Host: localhost:5010
Content-Length: 37

```JSON
{
  "offset": 0,
  "ustring": "met"
}
```

Response is a JSON object of the following form: 

```JSON
[
	{
		"comment": "The metre is a unit of length defined as the length of the path travelled by light in vacuum during a time interval of 1/299 792 458 of a second.",
		"name": "metre",
		"symbol": "m"
	},
	{
		"comment": "Candela per square metre is a unit of luminance defined as candela divided by square metre.",
		"name": "candela per square metre",
		"symbol": "cd/m"
	},
	{
		"comment": "Cubic metre is a unit of volume defined as the volume of a cube whose sides measure exactly one metre.",
		"name": "cubic metre",
		"symbol": "m3"
	},
]
```

## `POST /api/units/geturi`
Returns the uri of a certain units contained in the ontology OM. 

Example http request: 
POST /api/units/geturi HTTP/1.1
Content-Type: application/json
Host: localhost:5010
Content-Length: 22

```JSON
{
  "uname": "metre"
}
```

Response is a JSON object of the following form: 

```JSON
{
	"URI": "http://www.ontology-of-units-of-measure.org/resource/om-2/metre"
}
```

## `POST /api/units/validate´
Validates user typed in units. For example 'diametre' is no unit. 

Example http request:
POST /api/units/validate HTTP/1.1
Content-Type: application/json
Host: localhost:5010
Content-Length: 24

{
  "ustring": "metre"
}

Respose: true / false

## `POST /api/units/saveconcept´
Is an endpoint for saving concepts in the entity 'mdb_concepts' in the MDB. 

Example http request: 
POST /api/units/saveconcept HTTP/1.1
Content-Type: application/json
Host: localhost:5010
Content-Length: 97

{
  "name": "metre",
  "uri": "http://www.ontology-of-units-of-measure.org/resource/om-2/metre"
}

The response is a postgres status message, e.g., 

"\"INSERT 0 1\""

## `POST /api/units/savecolumnsconcept'
Saves values in the entity 'mdb\_columns\_concepts', which realizes the relation between 'mdb\_columns' and 'mdb\_concepts'. Make sure the concept is contained in 'mdb\_concepts'. 

Example http request:
POST /api/units/savecolumnsconcept HTTP/1.1
Content-Type: application/json
Host: localhost:5010
Content-Length: 122

{
  "cdbid": "1",
  "cid": "1",
  "tid": "1",
  "uri": "http://www.ontology-of-units-of-measure.org/resource/om-2/metre"
}

The response is a postgres status message, e.g., 

"\"INSERT 0 1\""

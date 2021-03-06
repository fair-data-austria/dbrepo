import os
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
import logging
import py_eureka_client.eureka_client as eureka_client
import json
from flasgger import Swagger
from flasgger.utils import swag_from
from flasgger import LazyString, LazyJSONEncoder
from list import list_units, get_uri
from validate import validator, stringmapper
from save import insert_mdb_concepts, insert_mdb_columns_concepts

app = Flask(__name__)
app.config["SWAGGER"] = {"title": "FDA-Units-Service", "uiversion": 3}

swagger_config = {
    "headers": [],
    "specs": [
        {
            "title": "units",
            "endpoint": "api-units",
            "route": "/api-units.json"
        }
    ],
    "static_url_path": "/flasgger_static",
    "swagger_ui": True,
    "specs_route": "/swagger-ui/",
}

template = dict(
    swaggerUiPrefix=LazyString(lambda: request.environ.get("HTTP_X_SCRIPT_NAME", ""))
)

app.json_encoder = LazyJSONEncoder
swagger = Swagger(app, config=swagger_config, template=template)

@app.route('/api/units/suggest', methods=["POST"], endpoint='suggest')
@swag_from('suggest.yml')
def suggest():
    input_json = request.get_json()
    try:
        unit = str(input_json['ustring'])
        offset = int(input_json['offset'])
        res = list_units(stringmapper(unit),offset)
        return jsonify(res), 200
    except Exception as e:
        print(e)
        res = {"success": False, "message": str(e)}
        return jsonify(res), 500

@app.route('/api/units/validate/<unit>', methods=["GET"], endpoint='validate')
@swag_from('validate.yml')
def valitate(unit):
    try:
        res = validator(unit)
        return str(res), 200
    except Exception as e:
        print(e)
        res = {"success": False, "message": str(e)}
        return jsonify(res)

@app.route('/api/units/uri/<uname>', methods=["GET"], endpoint='uri')
@swag_from('geturi.yml')
def geturi(uname):
    try:
        res = get_uri(uname)
        return jsonify(res), 200
    except Exception as e:
        print(e)
        res = {"success": False, "message": str(e)}
        return jsonify(res), 500

@app.route('/api/units/saveconcept', methods=["POST"], endpoint='saveconcept')
@swag_from('saveconcept.yml')
def saveconcept():
    input_json = request.get_json()
    try:
        uri = str(input_json['uri'])
        c_name = str(input_json['name'])
        if insert_mdb_concepts(uri, c_name) > 0:
            return jsonify({'uri': uri}), 201
        else:
            return jsonify({'status': 'error'}), 400
    except Exception as e:
        print(e)
        res = {"success": False, "message": str(e)}
        return jsonify(res), 500

@app.route('/api/units/savecolumnsconcept', methods=["POST"], endpoint='savecolumnsconcept')
@swag_from('savecolumnsconcept.yml')
def saveconcept():
    input_json = request.get_json()
    try:
        uri = str(input_json['uri'])
        cid = int(input_json['cid'])
        tid = int(input_json['tid'])
        cdbid = int(input_json['cdbid'])
        if insert_mdb_columns_concepts(cdbid, tid, cid, uri)>0:
            return jsonify({'uri': uri}), 201
        else:
            return jsonify({'status': 'error'}), 400
    except Exception as e:
        print(e)
        res = {"success": False, "message": str(e)}
        return jsonify(res), 500

rest_server_port = 5010
eureka_client.init(eureka_server=os.getenv('EUREKA_SERVER', 'http://localhost:9090/eureka/'),
                   app_name="fda-units-service",
                   instance_ip="fda-units-service",
                   instance_host="fda-units-service",
                   instance_port=rest_server_port)

if __name__ == '__main__':
    http_server = WSGIServer(('', 5010), app)
    http_server.serve_forever()

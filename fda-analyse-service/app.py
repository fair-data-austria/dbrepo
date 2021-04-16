import os
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
from determine_dt import determine_datatypes
#from werkzeug.utils import secure_filename
#from werkzeug import cached_property
import logging
import py_eureka_client.eureka_client as eureka_client
from os import environ
#from flask_swagger_ui import get_swaggerui_blueprint
#from flask_restful_swagger_3 import Api, Resource, swagger, Schema
import json
from flasgger import Swagger
from flasgger.utils import swag_from
from flasgger import LazyString, LazyJSONEncoder


#logging.basicConfig()
#UPLOAD_FOLDER = '.'
#ALLOWED_EXTENSIONS = {'csv'}

app = Flask(__name__)
app.config["SWAGGER"] = {"title": "FDA-Analyse-Service", "uiversion": 3}

swagger_config = {
    "headers": [],
    "specs": [
        {
            "endpoint": "api",
            "route": "/api.json",
            "rule_filter": lambda rule: True,  # all in
            "model_filter": lambda tag: True,  # all in
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

#TODO GET instead of POST  
@app.route('/determinedt', methods=["POST"])
@swag_from('determinedt.yml')
def determinedt(): 
    input_json = request.get_json()
    try: 
        filepath = str(input_json['filepath'])
        enum = False 
        if 'enum' in input_json:
            enum = bool(input_json['enum']) 
            print(enum)
        enum_tol = 0.001 
        if 'enum_tol' in input_json:
            enum_tol = float(input_json['enum_tol'])
            print(enum_tol)
        seperator = ','
        if 'seperator' in input_json: 
            seperator = str(input_json['seperator'])
        res = determine_datatypes(filepath,enum,enum_tol,seperator)
    except: 
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/importdatabase', methods=["POST"])
@swag_from('importdb.yml')
def importdb(): 
    input_json = request.get_json() 
    try: 
        res = ok 
    except: 
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/importdata', methods=["POST"])
@swag_from('importdata.yml')
def importdb1(): 
    input_json = request.get_json() 
    try: 
        res = ok 
    except: 
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/updatecolumns', methods=["POST"])
@swag_from('updatecol.yml')
def importdb2(): 
    input_json = request.get_json() 
    try: 
        res = ok 
    except: 
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

#@app.route('/exequery', methods=["GET"])
#@swag_from('importdb.yml')
#def importdb3(): 
#    input_json = request.get_json() 
#    try: 
#        res = ok 
#    except: 
#        res = {"success": False, "message": "Unknown error"}
#    return jsonify(res), 200
        
rest_server_port = 5000
eureka_client.init(eureka_server=os.getenv('EUREKA_SERVER', 'http://localhost:9090/eureka/'),
                   app_name="fda-analyse-service",
                   instance_port=rest_server_port)    
    
if __name__ == "__main__":
    app.run(host='0.0.0.0')
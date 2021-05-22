import os
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
from determine_dt import determine_datatypes
from analysecsv import analysecsv
from insert_mdb_db import insert_mdb_db
from import_db import import_db
from update_mdb_db_ispublic import insert_mdb_db_pub
from insert_mdb_tbl import insert_mdb_tbl
from insert_mdb_col import insert_mdb_col
from insert_mdb_col import update_mdb_siunit
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
@swag_from('/as-yml/determinedt.yml')
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
    except Exception as e: 
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/checkcsv', methods=["POST"])
@swag_from('/as-yml/checkcsv.yml')
def checkcsv(): 
    input_json = request.get_json() 
    try: 
        filepath = str(input_json['filepath'])
        intdbname = str(input_json['intdbname'])
        dbhost = str(input_json['dbhost'])
        dbid = int(input_json['dbid'])
        tname = str(input_json['tname'])
        header = True 
        if 'header' in input_json:
            header = bool(input_json['header']) 
        seperator = ','
        if 'seperator' in input_json: 
            seperator = str(input_json['seperator'])
        res = analysecsv(filepath,seperator,intdbname, dbhost, dbid, tname, header)
    except Exception as e: 
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/update_mdb_db', methods=["POST"])
@swag_from('/as-yml/importdb.yml')
def importdb(): 
    input_json = request.get_json() 
    try: 
        dbid=int(input_json['dbid'])
        resourcetype = str(input_json['resourcetype'])
        description = str(input_json['description'])
        publisher = str(input_json['publisher'])
        res = insert_mdb_db(dbid, resourcetype, description, publisher)
    except Exception as e:
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/update_mdb_tbl', methods=["POST"])
@swag_from('/as-yml/importtbl.yml')
def importtbl(): 
    input_json = request.get_json() 
    try: 
        dbid=int(input_json['dbid'])
        res = insert_mdb_tbl(dbid)
    except Exception as e:
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/update_mdb_db_ispublic', methods=["POST"])
@swag_from('/as-yml/updateispub.yml')
def updateispublic(): 
    input_json = request.get_json() 
    try: 
        dbid=int(input_json['dbid'])
        ispublic=bool(input_json['is_public'])
        res = insert_mdb_db_pub(dbid, ispublic)
    except Exception as e:
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/update_mdb_columns_num_siunit', methods=["POST"])
@swag_from('/as-yml/updatesiunit.yml')
def updatesiunit(): 
    input_json = request.get_json() 
    try: 
        dbid=int(input_json['dbid'])
        tid=int(input_json['tid'])
        cid=int(input_json['cid'])
        siunit=str(input_json['siunit'])
        res = update_mdb_siunit(dbid,tid,cid,siunit)
    except Exception as e:
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

@app.route('/update_mdb_col', methods=["POST"])
@swag_from('/as-yml/importcol.yml')
def importcol(): 
    input_json = request.get_json() 
    try: 
        dbid=int(input_json['dbid'])
        tid=int(input_json['tid'])
        res = insert_mdb_col(dbid,tid)
    except Exception as e:
        print(e)
        res = {"success": False, "message": "Unknown error"}
    return jsonify(res), 200

rest_server_port = 5000
eureka_client.init(eureka_server=os.getenv('EUREKA_SERVER', 'http://localhost:9090/eureka/'),
                   app_name="fda-analyse-service",
                   instance_port=rest_server_port)    
    
if __name__ == "__main__":
    app.run(host='0.0.0.0')

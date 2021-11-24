import os
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
import logging
import py_eureka_client.eureka_client as eureka_client
import json
from flasgger import Swagger
from flasgger.utils import swag_from
from flasgger import LazyString, LazyJSONEncoder

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

@app.route('/api/units/suggest', methods=["POST"], endpoint='units_suggest')
#@swag_from('/as-yml/suggest.yml')
def suggest():
    return 200

rest_server_port = 5010
eureka_client.init(eureka_server=os.getenv('EUREKA_SERVER', 'http://localhost:9090/eureka/'),
                   app_name="fda-units-service",
                   instance_ip="fda-units-service",
                   instance_host="fda-units-service",
                   instance_port=rest_server_port)

if __name__ == '__main__':
    http_server = WSGIServer(('', 5010), app)
    http_server.serve_forever()

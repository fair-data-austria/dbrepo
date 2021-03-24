import os
import uuid
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
from werkzeug.utils import secure_filename
from determine_dt import determine_datatypes
#from extract_tables import extract_tbl
from extract_sqlmetadata import extract_sqlmetadata
import logging
import py_eureka_client.eureka_client as eureka_client
from flask import Flask, flash, request, redirect, url_for, Response
from werkzeug.utils import secure_filename
from determine_dt import determine_datatypes
from os import environ


logging.basicConfig()
UPLOAD_FOLDER = '.'
ALLOWED_EXTENSIONS = {'csv'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/extract-metadata', methods=['POST'])
def extract_metadata():
    json = request.json
    if not json or not "cname" in json:
        abort(400)
    extract_sqlmetadata(json['cname'])

    return "OK",200
    

@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
            ## check if csv TODO
        if file:
            enum = False
            if request.args.get('enum'):
                enum = True
            enum_tol = 0.001
            if request.args.get('enum_tol') != None:
            	enum_tol = float(request.args.get('enum_tol'))
            filename = str(uuid.uuid1())
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            result = determine_datatypes(filename, enum, enum_tol)
            os.remove(filename)
            return result, 200
    else:
            return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name=file>
      <input type=submit value=Upload>
    </form>
    '''

@app.route('/datatypesbypath',methods=['GET'])
def datatypesbypath(): 
    enum = False 
    if request.args.get('enum'): 
    	enum = True 
    enum_tol = 0.001
    if request.args.get('enum_tol') != None:
        enum_tol = float(request.args.get('enum_tol'))
    path = request.args.get('filepath')
    result = determine_datatypes(path, enum,enum_tol)

    return jsonify(result), 200

@app.route('/extract-tables', methods=['POST'])
def extract_tables():
    print(request.json)
    if not request.json or not 'query' in request.json:
        abort(400)
    sql = request.json['query']

    # TODO add error handling in case of invalid SQL etc.
    return jsonify(extract_tbl(sql)), 200

rest_server_port = 5000
eureka_client.init(eureka_server=os.getenv('EUREKA_SERVER', 'http://localhost:9090/eureka/'),
                   app_name="fda-analyse-service",
                   instance_port=rest_server_port)


if __name__ == "__main__":
    app.run(host='0.0.0.0')






import os
import uuid
from flask import Flask, flash, request, redirect, url_for, Response, abort, jsonify
from werkzeug.utils import secure_filename
from determine_dt import determine_datatypes
from extract_tables import extract_tbl 

UPLOAD_FOLDER = '.'
ALLOWED_EXTENSIONS = {'csv'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

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
            filename = str(uuid.uuid1())
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            result = determine_datatypes(filename)
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

@app.route('/extract-tables', methods=['POST'])
def extract_tables():
    print(request.json)
    if not request.json or not 'query' in request.json:
        abort(400)
    sql = request.json['query']

    # TODO add error handling in case of invalid SQL etc.
    return jsonify(extract_tbl(sql)), 200

if __name__ == "__main__":
    app.run(host='0.0.0.0')
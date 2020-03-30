from flask import Flask,jsonify,request
from joblib import load
import requests
import cv2
import numpy as np
from PIL import Image

app = Flask(__name__)

@app.route("/", methods=["POST"])
def index():
    img = Image.open(request.files['file'])
    return 'Success!'

@app.route('/home/<path:url>',methods=['POST','GET'])
def home(url):
    clf = load('logit.joblib')

    img = Image.open(requests.get(url, stream=True).raw).convert('L')
    img_array = np.array(img)

    new_array = cv2.resize(img_array, (100, 100))

    new_array = new_array/255
    img = new_array.reshape(-1,100*100)
    result = clf.predict(img)

    return jsonify({'result':str(result[0])})

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=80,debug=True)



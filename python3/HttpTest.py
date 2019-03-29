from flask import Flask
from flask import jsonify,json,request,session
import GetData
import LedControl
import datetime

room1 = None
room2 = None
room3 = None
isAuto = None

app=Flask(__name__)


@app.route('/')
def home():
    return 'Home'

@app.route('/getledstatus')
def sendRealTimeData():
    data = LedControl.getData()
    jsonObj = data
    print(jsonObj)
    response = app.response_class(
        response = json.dumps(jsonObj),
        status = 200,
        mimetype='application/json'
    )
    
    return response

@app.route('/getdata')
def sendElectric():
    data = GetData.requestElec()
    jsonObj = {'used':data,'notused':100-data}
    print(jsonObj)
    response = app.response_class(
        response = json.dumps(jsonObj),
        status = 200,
        mimetype='application/json'
    )
    
    return response

@app.route('/postdata', methods=['POST'])
def ledControlManual():
    message ={'status':'sucecss'}
    jsonStr = json.dumps(message)
    response = app.response_class(
        response = json.dumps(message),
        status = 200,
        mimetype='application/json'
    )
    global room1,room2,room3,isAuto
    data = json.loads(request.data.decode('ascii'))
    room1 = data['room1']
    room2 = data['room2']
    room3 = data['room3']
    isAuto = data['status']
    LedControl.setData(room1,room2,room3,isAuto)
    print(data)
    return response

    

if __name__ == '__main__':
    app.run(host='192.168.55.170')
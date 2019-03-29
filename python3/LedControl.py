import RPi.GPIO as GPIO
import time
import sys
import GetData

dd=None
status = 'auto'
room1 = None
room2 = None
room3 = None

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(19,GPIO.OUT)
pwm_led1 = GPIO.PWM(19,500)
pwm_led1.start(0)

GPIO.setup(20,GPIO.OUT)
pwm_led2 = GPIO.PWM(20,500)
pwm_led2.start(0)

GPIO.setup(21,GPIO.OUT)
pwm_led3 = GPIO.PWM(21,500)
pwm_led3.start(0)

def setData(get1,get2,get3,getStatus):
   
    global room1,room2,room3,status
    room1 = get1
    room2 = get2
    room3 = get3
    status = getStatus
    changeLight()

def getData():
    return {"room1":room1,"room2":room2,"room3":room3,"status":status}

def changeLight():
    global GPIO, pwm_led1, pwm_led2, pwm_led3
    
    if status == 'auto':
        data = GetData.requestElec()
        pwm_led1.ChangeDutyCycle(data)
        pwm_led2.ChangeDutyCycle(data)
        pwm_led3.ChangeDutyCycle(data)
        
    if status == 'manual':
        print('section2')
        print(room1,room2,room3,status)
        pwm_led1.ChangeDutyCycle(float(room1*10))
        pwm_led2.ChangeDutyCycle(float(room2*10))
        pwm_led3.ChangeDutyCycle(float(room3*10))
        
    
##GPIO.cleanup()
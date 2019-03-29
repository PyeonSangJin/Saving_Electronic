from bs4 import BeautifulSoup
import urllib.request as req
import RPi.GPIO as GPIO
import time
import threading
import subprocess
import LedControl
from gpiozero import PWMLED

data = 100
pwm_led = None
led = PWMLED(20)



def requestElec():
    global data
    while True:
        res = req.urlopen('http://www.kpx.or.kr/www/contents.do?key=217')

        soup = BeautifulSoup(res,'html.parser')

        stream = soup.select_one('#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-of-type(4) > td').string
        #print(percent)
        percent = stream.split(' ')
        data = float(percent[0])
        time.sleep(5)

"""        
GPIO.setmode(GPIO.BCM)
GPIO.setup(20, GPIO.OUT)
pwm_led = GPIO.PWM(20,GPIO.HIGH)
pwm_led.start(0)
GPIO.setwarnings(False)
"""
def controlLed():
    global data
    global pwm_led
    global led
    
    while True:
        led.value = 1
##        GPIO.output(20,GPIO.HIGH)
##        LedControl.control(data,pwm_led)
        print(data)
        time.sleep(2)
       

t = threading.Thread(target = requestElec)
t.start()

t1 = threading.Thread(target = controlLed)
t1.start()
    
GPIO.cleanup()

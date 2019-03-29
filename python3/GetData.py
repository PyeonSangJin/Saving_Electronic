from bs4 import BeautifulSoup
import urllib.request as req

def requestElec():
    data = None
    res = req.urlopen('http://www.kpx.or.kr/www/contents.do?key=217')

    soup = BeautifulSoup(res,'html.parser')

    stream = soup.select_one('#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-of-type(4) > td').string
    #print(percent)
    percent = stream.split(' ')
    data = float(percent[0])
        
    return data
        
def requestRealTime():
    power_data = None
    percent_data = None
    
    res = req.urlopen('http://www.kpx.or.kr/www/contents.do?key=217')
    soup = BeautifulSoup(res,'html.parser')
    
    power_selector =  soup.select_one('#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-of-type(3) > td').string
    percent_selector =  soup.select_one('#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-of-type(4) > td').string
    
    power = power_selector.split(' ')
    percent = percent_selector.split(' ')
    
    return {"power":power,"percent":percent}
import cgi
import wsgiref
import logging
import datetime
import time

from geohash import Geohash
from shardedcounter import *
from boundsSplitting import *

from urlparse import urlparse

from django.utils import simplejson

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

class BikeBingle(db.Model):
    occuredOn = db.DateTimeProperty()
    enteredOn = db.DateTimeProperty(auto_now_add=True)
    
    position = db.GeoPtProperty()
    description = db.TextProperty()
    
    enteredBy = db.UserProperty()
    
    injuryIndex = db.IntegerProperty()
    type = db.IntegerProperty()
    link = db.LinkProperty()
    geoHash = db.StringProperty()

class BikeBingleHighRes(db.Model):
    type = db.IntegerProperty()
    position = db.GeoPtProperty()
    geoHash = db.StringProperty()

class BikeBingleMediumRes(db.Model):
    type = db.IntegerProperty()
    position = db.GeoPtProperty()
    geoHash = db.StringProperty()

class BikeBingleLowRes(db.Model):
    type = db.IntegerProperty()
    position = db.GeoPtProperty()
    geoHash = db.StringProperty()

def getQueryForLengthSquared(lengthSquared):
    if lengthSquared < 0.015:
        return BikeBingle.all()
    elif lengthSquared < 0.8:
        return BikeBingleHighRes.all()
    elif lengthSquared < 800:
        return BikeBingleMediumRes.all()
    else:
        return BikeBingleLowRes.all()

class test1(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        #self.response.out.write('Hello, webapp World!')
        
        #the name will be obtained from a URL something like
        # this http://localhost:8080/server/?name=ImReallyGood
        name = self.request.get("name")
        self.response.out.write('hello ' + name)

class DeleteBingle(webapp.RequestHandler):
    def post(self):
        
        respBody = 'false'
        
        bingleId = self.request.body
        logging.info("request to delete a bingle with id :" + bingleId + ":")
        
        if len(bingleId) == 0:
            #no bingle id given so return
            respBody = 'false'
        else:
            currentUser = users.get_current_user()
            if not currentUser:
                #no user logged in so return
                logging.warn("no user logged in, cannot delete from db")
            else:
                entity = BikeBingle.get_by_id(int(bingleId))
                if not entity:
                    #no entity returnefd as it mustn't be in the DB so just return
                    logging.warn("no entity found with key " + bingleId)
                else:
                    if currentUser != entity.enteredBy:
                        #then the user that entered this entity is not the one trying to delete it
                        logging.warn("current user does not match user trying to delete item")
                    else:
                        ChangeCountForBingleType(entity.type,-1)
                        entity.delete()
                        respBody = 'true'
        
        
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.headers['Content-Length'] = len(respBody)
        
        self.response.out.write(respBody)
        
        

class AddBingle(webapp.RequestHandler):
    def post(self):
        args = simplejson.loads(self.request.body)
        
        aBingle = self.getBingleFromJsonData(args)
        
        #calculate the geohash value for the position
        latitude = aBingle.position.lat
        longitude = aBingle.position.lon
        #yes it needs double brackets around the lat,long
        pthash = Geohash((latitude,longitude))
        aBingle.geoHash = str(pthash)
        aBingle.put()
        ChangeCountForBingleType(aBingle.type,1)
        
        #now add it to the low res table
        self.addToHighResIfNecessary(latitude,longitude)
        
        logging.info("bingle added to database with id" + str(aBingle.key().id()))
        
    def getRoundedValueHigh(self, latOrLng):
        return round(latOrLng,2)
        
    def getRoundedValueMedium(self, latOrLng):
        return round(latOrLng,1)
        
    def getRoundedValueLow(self, latOrLng):
        return round(latOrLng/2.)*2

    def addToHighResIfNecessary(self,lat,lng):
        latitude = self.getRoundedValueHigh(lat)
        longitude = self.getRoundedValueHigh(lng)
        
        pthash = Geohash((latitude,longitude))
        geoHashString = str(pthash)
        
        q = BikeBingleHighRes.all()
        q.filter("geoHash =", geoHashString)
        res = q.fetch(1)
        
        #logging.info("found LODs" + str(len(res)))
        
        if len(res) == 0:
            logging.info("added high res")
            bblr = BikeBingleHighRes(
                                     position=db.GeoPt(lat,lng),
                                     geoHash = geoHashString,
                                     type = -2
                                    )
            bblr.put()
            self.addToMediumResIfNecessary(lat, lng)

    def addToMediumResIfNecessary(self,lat,lng):
        latitude = self.getRoundedValueMedium(lat)
        longitude = self.getRoundedValueMedium(lng)
        
        pthash = Geohash((latitude,longitude))
        geoHashString = str(pthash)
        
        q = BikeBingleMediumRes.all()
        q.filter("geoHash =", geoHashString)
        res = q.fetch(1)
        
        #logging.info("found LODs" + str(len(res)))
        
        if len(res) == 0:
            logging.info("added med res")
            bblr = BikeBingleMediumRes(
                                       position=db.GeoPt(lat,lng),
                                       geoHash = geoHashString,
                                       type = -2
                                      )
            bblr.put()
            self.addToLowResIfNecessary(lat, lng)

        
    def addToLowResIfNecessary(self,lat,lng):
        latitude = self.getRoundedValueLow(lat)
        longitude = self.getRoundedValueLow(lng)
        
        pthash = Geohash((latitude,longitude))
        geoHashString = str(pthash)
        
        q = BikeBingleLowRes.all()
        q.filter("geoHash =", geoHashString)
        res = q.fetch(1)
        
        #logging.info("found LODs" + str(len(res)))
        
        if len(res) == 0:
            logging.info("added low res")
            bblr = BikeBingleLowRes(
                                    position=db.GeoPt(lat,lng),
                                    geoHash = geoHashString,
                                    type = -2
                                    )
            bblr.put()
        
        
    def makeLinkValid(self, url):
        if len(url) == 0:
            return url
        elif url.startswith('http://'):
            return url
        else:
            return 'http://' + url
        
        
    def getBingleFromJsonData(self, jsonData):
        
        occuredOnDate =  datetime.datetime.strptime(jsonData['occuredOn'], "%Y-%m-%d %H:%M:%S");
        desc = jsonData['description']
        
        pos = jsonData['position']
        lat = pos['lat']
        lng =pos['lng']
        
        injury = jsonData['injuryIndex']
        typet = jsonData['type']
        linkt = jsonData['link']
        linkt = self.makeLinkValid(linkt)
        
        
        if len(linkt) == 0:
            centerBB = BikeBingle(enteredBy=users.get_current_user(),
                                  occuredOn=occuredOnDate,
                                  enteredOn=datetime.datetime.today(),
                                  description=desc,
                                  position=db.GeoPt(lat,lng),
                                  injuryIndex=injury,
                                  type=typet
                                  )
        else:
            centerBB = BikeBingle(enteredBy=users.get_current_user(),
                                  occuredOn=occuredOnDate,
                                  enteredOn=datetime.datetime.today(),
                                  description=desc,
                                  position=db.GeoPt(lat,lng),
                                  injuryIndex=injury,
                                  type=typet,
                                  link=linkt
                                  )
        return centerBB
        
        
class GetBingleCount(webapp.RequestHandler):
    def get(self):
        result = 0
        
        bingleType = self.request.get("type")
        if bingleType:
            result = GetCountForBingleType(bingleType)
        else:
            result = GetCountForAllBingleTypes()
        
        respBody = str(result)
        
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.headers['Content-Length'] = len(respBody)
        self.response.out.write(respBody)
        
class GetLatestBingles(webapp.RequestHandler):
    def get(self):
        
        bingles = self.getLatestBingles()
        
        list = []
        for aBingle in bingles:
            list.append(getBingleAsSimplePythonObject(aBingle))
        
        data = simplejson.dumps(list,indent=4)
        self.response.headers['Content-Length'] = len(data)
        self.response.out.write(data)


    def getLatestBingles(self):
        q = BikeBingle.all()
        q.order("-enteredOn")
        res = q.fetch(10)
        return res

class GetBingles(webapp.RequestHandler):
    def __init__(self):
        self.__isLowRes = False
    
    def get(self):
        

        start_time = datetime.datetime.now()
        
        #url would be something like
        #  http://localhost:8080/getbingles/?neLatitude=1.23456&neLongitude=2.3456789&swLatitude=3.456789012&swLongitude=4.567890123
        
        neLat = self.request.get("neLatitude")
        neLng = self.request.get("neLongitude")
        swLat = self.request.get("swLatitude")
        swLng = self.request.get("swLongitude")
        isUserOnly = self.request.get("isuseronly")
        isLowRes = self.request.get("islowres")
        
        #if not isLowRes:
        #    self.__isLowRes = False
        #elif isLowRes == 'true':
        #    self.__isLowRes = True
        #else:
        #    self.__isLowRes = False
        #respString = 'ne=' + neLat + ", " + neLng + "   sw=" + swLat + ", " + swLng
        self.__isLowRes = True

        #logging.info("getting URL params = " + str(datetime.datetime.now() - start_time))

        #start_time = datetime.datetime.now()

        if not isUserOnly:
            bingles = self.getBingles(neLat, neLng, swLat, swLng)
        elif isUserOnly == 'true':
            bingles = self.getBinglesForCurrentUser()
        else:
            bingles = self.getBingles(neLat, neLng, swLat, swLng)
        
        #logging.info("getting bingles = " + str(datetime.datetime.now() - start_time))
        #start_time = datetime.datetime.now()
        
        list = []
        for aBingle in bingles:
            list.append(getBingleAsSimplePythonObject(aBingle))
        
        #logging.info("bingles to simple python = " + str(datetime.datetime.now() - start_time))
        #start_time = datetime.datetime.now()
        
        ##logging.info("found bingles count = " + str(len(list)))
        data = simplejson.dumps(list,indent=4)
        #logging.info('generated JSON: ' + data)
        
        #logging.info("simple python to json str = " + str(datetime.datetime.now() - start_time))
        
        #self.response.headers['Content-Type'] = 'text/plain'
        self.response.headers['Content-Length'] = len(data)
        self.response.out.write(data)
        
    def getBingles(self, neLat, neLng, swLat, swLng):
        fneLat = float(neLat)
        fneLng = float(neLng)
        fswLat = float(swLat)
        fswLng = float(swLng)
        
        start_time = datetime.datetime.now()
        
        res = []
        splitBounds = getSplitBounds(fneLat, fneLng, fswLat, fswLng)
        lengthSquared = getLengthSquaredOfSplitBounds(splitBounds)
        
        #logging.info("length squared = " + str(lengthSquared))
        #logging.info("   getting split bounds = " + str(datetime.datetime.now() - start_time))
        #logging.info('no of split bounds = '+str(len(splitBounds)))
        
        for sb in splitBounds:

            ineLat, ineLng, iswLat, iswLng = sb
            
            #logging.info(str(sb))
            
            neGeoHash = Geohash((ineLat,ineLng))
            swGeoHash = Geohash((iswLat,iswLng))
        
            if self.__isLowRes:
                #q = BikeBingleLowRes.all()
                q = getQueryForLengthSquared(lengthSquared)
            else:
                q = BikeBingle.all()
            q.filter("geoHash <", str(neGeoHash))
            q.filter("geoHash >", str(swGeoHash))
        
            greetings = q.fetch(500) 
            
            #logging.info('no found from db  = '+str(len(greetings)))
            res = res + greetings

                #for aBingle in greetings:
                #    #if (aBingle.position.lat < ineLat) & 
                #        (aBingle.position.lat > iswLat) & 
                #        (aBingle.position.lon < ineLng) & (aBingle.position.lon > iswLng):
                #    res.append(aBingle)
                #logging.info('no found from db after strip = '+str(len(res)))
            
            
        return res
        
        
    
    def getBinglesForCurrentUser(self):
        user = users.get_current_user()
        if not user:
            return []
        
        q = BikeBingle.all()
        q.filter("enteredBy =", user)
        q.order("enteredOn")
        
        res = q.fetch(500)
        return res

    
def getBingleAsSimplePythonObject(aBingle):
    if aBingle.type == -2:
        bingleData = {'id': aBingle.key().id(),
                      'position': {'lat':aBingle.position.lat,'lng':aBingle.position.lon},
                      'type':aBingle.type
                     }
        return bingleData
    else:
        #userEmail = aBingle.enteredBy.email()
        #userNickName = aBingle.enteredBy.nickname()
        #TODO - figure out what I was meant to do with this
        #userEmail = ''
        #userNickName = ''
        
        bingleData = {'id': aBingle.key().id(),
                      'description': str(aBingle.description),
                      'occuredOn': str(aBingle.occuredOn),
                      'enteredOn': str(aBingle.enteredOn),
                      'position': {'lat':aBingle.position.lat,'lng':aBingle.position.lon},
                      #'enteredBy': {'name':userNickName,'email':userEmail},
                      'injuryIndex': aBingle.injuryIndex,
                      'type':aBingle.type,
                      'link':str(aBingle.link)
                     }
        return bingleData
  
    

class LoggedInUser(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()

        if user:
            logging.info(user.email() + ' logged in')
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write(user.nickname() + ' ' + user.email())
        else:
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write('')


class LoginPage(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()

        tabName = self.request.get("tabname")
        if not tabName:
            tabName = 'add'

        if user:
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write(user.nickname() + ' ' + user.email() + ' isLoggedIn')
        else:
            o = urlparse(self.request.uri)
            rootUrl = o[0] + "://" + o[1] + "/"
            queryBit = "?tab=" + tabName
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write(users.create_login_url(rootUrl + queryBit))
            
            #self.redirect(users.create_login_url(self.request.uri))

class LogoutPage(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()

        if user:
            self.redirect(users.create_logout_url(self.request.uri))
        else:
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write('You are not logged in ');
            
class Cleanup(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()

        if user:
            if users.is_current_user_admin():
                #self.__doCleanup()
                self.response.headers['Content-Type'] = 'text/plain'
                self.response.out.write('remember lock, you removed this functionality to prevent accidents');
            else:
                self.response.headers['Content-Type'] = 'text/plain'
                self.response.out.write('you are not an administrator');
        else:
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write('no user logged in');
                
    def __doCleanup(self):
        logging.info('doing cleanup')
        res = CounterShard.all().fetch(500)
        if len(res) != 0:
            db.delete(res)
        else:
            logging.info('counter shard empty')
            
        res = BikeBingleLowRes.all().fetch(500)
        if len(res) != 0:
            db.delete(res)
        else:
            logging.info('BikeBingleLowRes empty')
            
        res = BikeBingleMediumRes.all().fetch(500)
        if len(res) != 0:
            db.delete(res)
        else:
            logging.info('BikeBingleMediumRes empty')
            
        res = BikeBingleHighRes.all().fetch(500)
        if len(res) != 0:
            db.delete(res)
        else:
            logging.info('BikeBingleHighRes empty')
        
        res = BikeBingle.all().fetch(500)
        if len(res) != 0:
            db.delete(res)
        else:
            logging.info('BikeBingle empty')
            logging.info('cleanup complete')
            self.response.headers['Content-Type'] = 'text/plain'
            self.response.out.write('cleanup complete');
        
            

application = webapp.WSGIApplication(
                                     [('/server/',test1),
                                      ('/getbingles/',GetBingles),
                                      ('/getlatestbingles/',GetLatestBingles),
                                      ('/addbingle/',AddBingle),
                                      ('/deletebingle/',DeleteBingle),
                                      ('/getbinglecount/',GetBingleCount),
                                      ('/login/',LoginPage),
                                      ('/logout/',LogoutPage),
                                      ('/cleanup/',Cleanup),
                                      ('/user/',LoggedInUser)],
                                     debug=True
                                    )

def main():
    run_wsgi_app(application)
    #wsgiref.handlers.CGIHandler.run(application)


if __name__ == "__main__":
    main()
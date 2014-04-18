#!/usr/bin/python

from google.appengine.ext import db
from google.appengine.api import memcache

import random

SHARDS_PER_COUNTER = 20

class CounterShard(db.Model):
    name = db.StringProperty(required=True)
    count = db.IntegerProperty(default=0)

def ChangeCount(nameOfCounter, delta):
    shard_id = '/%s/%s' % (nameOfCounter, random.randint(1, SHARDS_PER_COUNTER))
    def update():
        shard = CounterShard.get_by_key_name(shard_id)
        if shard:
            shard.count += delta
        else:
            shard = CounterShard(key_name=shard_id, name=nameOfCounter, count=delta)
        shard.put()
    db.run_in_transaction(update)

def GetCount(nameOfCounter):
    memcache_id = '/CounterShard/%s' %  nameOfCounter
    result = memcache.get(memcache_id)
    if not (result == None):
        return result
    result = 0
    for shard in CounterShard.gql('WHERE name=:1', nameOfCounter):
        result += shard.count
    memcache.set(memcache_id, result, 60)
    return result

def __getCounterNameForBingleType(bingleTypeId):
    return "bct_" + str(bingleTypeId) 

def GetCountForAllBingleTypes():
    #gets all shards from the database
    q = CounterShard.all()
    allshards = q.fetch(1000)
    
    result = 0
    for shard in allshards:
        result += shard.count
    return result

def GetCountForBingleType(bingleTypeId):
    counterName = __getCounterNameForBingleType(bingleTypeId)
    return GetCount(counterName)

def ChangeCountForBingleType(bingleTypeId,delta):
    counterName = __getCounterNameForBingleType(bingleTypeId)
    return ChangeCount(counterName,delta)


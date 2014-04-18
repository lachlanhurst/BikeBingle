#!/usr/bin/python
"""
 Simple little module to help with the division of a given bounds
 so that it plays well with geohash.
"""
def getSplitBounds(neLat, neLng, swLat, swLng):
        start = []
        start.append((neLat, neLng, swLat, swLng))
        res = getSplitBoundsAcrossDateLine(start)
        res = getSplitBoundsAcross90Intervals(res)
        res = getSplitBoundsAcrossEquator(res);
        return res

def getSplitBoundsAcrossEquator(toSplit):
    res = []
    
    for sb in toSplit:
        north ,east, south, west = sb
        
        if (north > 0) & (south < 0):
            #then it extends across the date line
            res.append((north,east,0,west))
            res.append((0,east,south,west))
        else:
            res.append((north,east,south,west))
    return res;

def getSplitBoundsAcross90Intervals(toSplit):
    res = []
    
    for sb in toSplit:
        north ,east, south, west = sb
        
        if (east > 90.0) & (west < -90.0):
            res.append((north,east,south,90.0))
            res.append((north,90.0,south,0.0))
            res.append((north,0.0,south,-90.0))
            res.append((north,-90.0,south,west))
        elif (east > 0) & (west < -90):
            res.append((north,east,south,0))
            res.append((north,0.0,south,-90.0))
            res.append((north,-90.0,south,west))
        elif (east > 90) & (west < 0):
            res.append((north,east,south,90.0))
            res.append((north,90.0,south,0.0))
            res.append((north,0.0,south,west))
        elif (east > 90) & (west < 90):
            res.append((north,east,south,90.0))
            res.append((north,90.0,south,west))
        elif (east > 0) & (west < 0):
            res.append((north,east,south,0.0))
            res.append((north,0.0,south,west))
        elif (east > -90) & (west < -90):
            res.append((north,east,south,-89.9999999))
            res.append((north,-90.0000001,south,west))
        else:
            res.append((north,east,south,west))
            
            
            
    return res;

def getSplitBoundsAcrossDateLine(toSplit):
    res = []
    
    for sb in toSplit:
        north ,east, south, west = sb
        
        if east < west:
            #then it extends across the date line
            res.append((north,east,south,-180.0))
            res.append((north,180.0,south,west))
        else:
            res.append((north,east,south,west))
    return res;

def getLengthSquaredOfSplitBounds(splitBounds):
    total = 0
    for sb in splitBounds:
        north ,east, south, west = sb
        
        width = east - west
        height = north - south
        
        total = total + width*width + height*height
    return total

#def main():
#    neLatitude = -23.271521484574702
#    neLongitude = -162.39111328124997
#    swLatitude = -50.73215935710591
#    swLongitude = 131.58544921875

#    spiltBounds = getSplitBounds(neLatitude, neLongitude, swLatitude, swLongitude)

#    for sb in spiltBounds:
#        print sb




#if __name__ == "__main__":
#    main()

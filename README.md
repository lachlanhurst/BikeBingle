![BikeBingle](./src-java/com/lachlanhurst/public/MainTitle.png)

BikeBingle was a bicycle accident tracking web-site written mid 2008, it continues to be hosted on Google's App Engine engine but recieves little usage. The source code is being posted up in this repository as open-soruce, in case it is of  use.

The running site can be found here [bikebingle.appspot.com](http://bikebingle.appspot.com/).

There's a post on the background behind the development of BikeBingle [on my blog](http://lockies.blogspot.com.au/2009/04/bikebingle.html).

## Development

### Client

The client was written using GWT, with the source code stored under `src-java`. The latest compiled version (Java -> JS) of this code is committed under the `src/gwt` directory to ease deployment. Compilation was performed with an early version of GWT.

### Server

BikeBingle itself is hosted on Google App Engine, including the hosting of all static files. The Python code for the server including all web-request handlers is included in the `src` directory.  BikeBingle runs on version 1 of the App Engine platform.

## Licensing

Apache V2 (as per LICENSE file), with the exception of the GeoHash implementation (geohash.py) which was released as public domain code (see file header).


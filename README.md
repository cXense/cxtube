[![Build Status](https://travis-ci.org/cXense/cxtube.svg?branch=master)](https://travis-ci.org/cXense/cxtube)
## What CxTube is?
* It is a mobile client for demo [website](http://tv.cxvid.io/demo/) 
* It is a sample application that was initially designed and implemented with two main objectives:
  * To demonstrate Cxense products on mobile platform in action
  * To show ways of working with Cxense mobile SDKs

## How-to build
1. Rename `api.properties.example` into `api.properties`
1. Put correct values into `api.properties`
1. Rename `keystore.properties.example` into `keystore.properties`
1. Put correct values for release signing into `keystore.properties`
1. Run `gradlew build`

## Where to find values for properties
1. `SITE_ID` - get it from Cxense Insight
1. `WIDGET_ID` - get it from Cxense Content
1. `ADSPACE_ID` - get it from Cxense Ad
1. `VIDEO_API_KEY` - get it from Cxense Support
1. `HOST_DOMAIN_URL` - set demo site domain for App Deep links (we use `tv.cxvid.io`)
1. `HOST_PATH_URL` - set relative path to main page of demo site (we use `/demo/`)
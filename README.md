React ScalaJS ScalaTags template for Lightbend Activator
==================================================

Modified [play-scala-scalajs-scalatags](https://github.com/oswaldo/play-scala-scalajs-scalatags) including elements from [scalajs-react-template](https://github.com/chandu0101/scalajs-react-template).

## Requirements

* A running cassandra instance

```
docker run --name some-cassandra -d -p 9042:9042 cassandra:3.7
```

* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Lightbend Activator](https://www.lightbend.com/activator/download)
* [webpack](http://webpack.github.io/docs/installation.html)

## Building and running

First time after checkout

```
cd configtool-poc/play
npm install
webpack
cd ..
```

Then in the project folder run with

```
activator
~run
```

> The project setup can/should be improved to avoid the npm/webpack stuff which 
> are there only to download and pack js libraries as expected by the scala modules

## TODO

* More documentation
* ...

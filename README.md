# FAIR Data Austria Services

## Build

Get the latest version of the fda-services repository and pull the submodule changes:

```bash
git submodule update --init --recursive
```

Everything is handled by compose, just build it by running:

```bash
docker-compose build
```

## Start

Now start all services by running:

```bash
docker-compose up
```

### Troubleshooting

Ubuntu 20.04 LTS

Use openjdk-11...openjdk-8 does not work.

Do not use maven provided my Ubuntu 20.4 LTS. It cannot handle those injections spring-boot wants it to do.

It says: WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)

Install maven from Apache Org.:

Download maven e.g. 3.6.3

```bash
wget https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp
```

Untar downloaded file to /opt

```bash
sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
```

Install the alternative version for the mvn in your system

```bash
sudo update-alternatives --install /usr/bin/mvn mvn /opt/apache-maven-3.6.3/bin/mvn 363
```

Check if your configuration is ok. You may use your current or the 3.6.3 whenever you wish, running the command below.

```bash
sudo update-alternatives --config mvn
```

cd /app

echo "Compiling..."
javac -d bin/ src/test.java

mkdir bin
cd bin

echo "Started"
java test
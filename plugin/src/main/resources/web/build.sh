rm -rf ./compiled
tsc
cd src
find . -name '*.html' | cpio -pdm ../compiled
find . -name '*.css' | cpio -pdm ../dist
find . -name '*.svg' | cpio -pdm ../dist
find . -name '*.png' | cpio -pdm ../dist
cp ./index.html ../dist/index.html
cd ..
npm run build

#!/bin/bash

# This installer is a quick way to get Khrysalis up and running on your computer.
# It will skip entirely if KHRYSALIS_META_LOCATION is already set.

if [ -v KHRYSALIS_META_LOCATION ]; then
  echo "Installing Khryalis and Butterfly..."
  git clone https://github.com/lightningkite/khrysalis-meta.git ~/khrysalis-meta
  echo "Clone complete."
  ~/khrysalis-meta/firstTime.sh
fi
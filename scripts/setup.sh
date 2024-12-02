set -e

install_jenv() {
    if ! grep -q jenv "$1"; then
      echo "export PATH=""$HOME"/.jenv/bin:"$PATH""" >> "$1"
      echo "eval "$(jenv init -)"" >> "$1"
      $("$2 $1")
    fi
}

echo '--- StarterApp Setup Script ---'
echo '--- Make sure homebrew is installed ---'

if ! brew --version; then
  curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh
else
  brew update
fi

echo '--- Installing brew packages ---'
brew install --cask temurin@21
brew install jenv gradle postgresql@14 node

# Install jenv in either the .bashrc or zshrc, whichever is present
if [ -f ~/.bashrc ]; then
  install_jenv "$HOME/.bashrc" "sh"
elif [ -f ~/.zshrc ]; then
  install_jenv "$HOME/.zshrc" "zsh"
else
  echo 'No shell config file found, cant install jenv'
  echo 'Please create a .bashrc or .zshrc'
  exit 1
fi

echo '--- Install localhost cert ---'
sh ./scripts/generate_localhost_cert.sh

# M1 Mac install stuff
if [[ $(uname -m) == 'arm64' ]]; then
  export PATH="$HOME/.jenv/bin:$PATH"
  export JENV_ROOT="/opt/homebrew/Cellar/jenv/"
  eval "$(/opt/homebrew/bin/brew shellenv)"
  eval "$(jenv init -)"
fi

echo '--- Install Java --- '

# Check if jenv can find java 21
if ! jenv versions | grep -q 21; then
  jenv add /Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
fi

echo '--- Start postgres service --- '

# If the postgres service isn't running in brew, start it
if ! brew services list | grep postgresql@14 | grep started; then
  brew services restart postgresql@14
fi

echo '--- Create databases --- '

# Create il-gcc database and user in postgres, if they don't exist
if ! psql -lqt | cut -d \| -f 1 | tr -d ' ' | grep -qx il-gcc; then
  printf "Creating database il-gcc\n"
  createdb il-gcc
  createuser -s il-gcc
fi

# Create il-gcc-test database and user in postgres, if they don't exist
if ! psql -lqt | cut -d \| -f 1 | tr -d ' ' | grep -qx il-gcc-test; then
  printf "Creating database il-gcc-test\n"
  createdb il-gcc-test
  createuser -s il-gcc-test
fi

echo '--- Run tests --- '

./gradlew clean test

echo '--- StarterApp Setup Script Complete ---'

set -e

# Make sure homebrew is installed
if ! brew --version; then
  curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh
else
  brew update
fi

# Install mkcert dependencies
brew install mkcert nss

# Start mkcert and create a new cert
mkcert -install
mkcert -pkcs12 localhost 127.0.0.1 ::1

# Check if password is in .env file
if [ -f ".env" ]; then
    # Search for the key in the .env file
    if grep -q "^KEY_STORE_PASSWORD=[^[:space:]]\+" ".env"; then
        echo "🟢 localhost cert is ready for development"
    else
        echo "🟡 Look for KEY_STORE_PASSWORD in our LastPass folder and add it to your .env file."
    fi
else
    echo "🔴 The .env file does not exist. Please create one, you can reference sample.env"
fi

echo "✅ localhost cert now installed"

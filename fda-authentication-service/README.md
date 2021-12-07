# Authentication Service

Uses SAML2.0

## Run

The container needs the environment variable set with the Key Store password, put it in your `~/.bashrc`:

```bash
export KEY_STORE_PASSWORD=...
```

or for fish in your `~/.config/fish/config.fish`:

```fish
set KEY_STORE_PASSWORD "..."
```

## Key Store

The key store is a secure container that contains the SSL/TLS certificate:

1. Let's Encrypt private key for `dbrepo.ossdip.at` with alias `ssl`

## Development

Context metadata for IdP: `http://localhost:9097/saml/metadata`
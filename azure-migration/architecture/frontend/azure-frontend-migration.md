# Ritual Growth — Azure Frontend Migration

## Current frontend

The frontend is a separate Next.js/React application:

- Repository: `ritual-growth-ui`
- Next.js
- React
- Firebase Web SDK
- Containerized with Node.js Alpine

The existing frontend repository is not modified by this Azure migration workspace.

## Existing API configuration

The frontend already supports:

`NEXT_PUBLIC_API_BASE_URL`

The Dockerfile accepts this value as a build argument:

```text
NEXT_PUBLIC_API_BASE_URL
        ↓
Dockerfile ARG
        ↓
Dockerfile ENV
        ↓
npm run build
        ↓
Next.js production image

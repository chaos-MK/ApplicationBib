resource "kubernetes_manifest" "vault_injector_bootstrap_certificate" {
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "Certificate"

    metadata = {
      name      = "vault-injector-bootstrap-ca"
      namespace = "vault"
    }

    spec = {
      isCA       = true
      commonName = "vault-agent-injector-bootstrap-ca"

      secretName = "vault-injector-bootstrap-ca"

      privateKey = {
        algorithm = "RSA"
        size      = 2048
      }

      issuerRef = {
        name = "vault-injector-bootstrap-selfsigned"
        kind = "ClusterIssuer"
      }
    }
  }

  depends_on = [
    helm_release.cert_manager,
    kubernetes_namespace.vault,
    kubernetes_manifest.vault_injector_bootstrap_selfsigned_issuer
  ]
}

resource "kubernetes_manifest" "vault_injector_bootstrap_selfsigned_issuer" {
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "ClusterIssuer"

    metadata = {
      name = "vault-injector-bootstrap-selfsigned"
    }

    spec = {
      selfSigned = {}
    }
  }

  depends_on = [
    helm_release.cert_manager
  ]
}

resource "kubernetes_manifest" "vault_injector_ca_issuer" {
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "Issuer"

    metadata = {
      name      = "vault-injector-ca"
      namespace = "vault"
    }

    spec = {
      ca = {
        secretName = "vault-injector-bootstrap-ca"
      }
    }
  }

  depends_on = [
    kubernetes_manifest.vault_injector_bootstrap_certificate
  ]
}

resource "kubernetes_manifest" "vault_injector_certificate" {
  field_manager {
    force_conflicts = true
  }
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "Certificate"

    metadata = {
      name      = "vault-agent-injector"
      namespace = "vault"
    }

    spec = {
      secretName = "vault-agent-injector-tls"

      duration    = "24h"
      renewBefore = "2h"

      commonName = "vault-agent-injector-svc"

      dnsNames = [
        "vault-agent-injector-svc",
        "vault-agent-injector-svc.vault",
        "vault-agent-injector-svc.vault.svc"
      ]

      privateKey = {
        algorithm      = "RSA"
        size           = 2048
        rotationPolicy = "Always"
      }

      issuerRef = {
        name = "vault-injector-ca"
        kind = "Issuer"
      }

      usages = [
        "digital signature",
        "key encipherment",
        "server auth"
      ]
    }
  }

  depends_on = [
    kubernetes_manifest.vault_injector_ca_issuer
  ]
}

AuditLedger

O AuditLedger é um sistema de registro de eventos projetado para garantir integridade, imutabilidade e autenticidade dos dados. Cada evento é assinado digitalmente e ligado ao evento anterior por meio de um hash, formando uma cadeia verificável.

Ele funciona como um livro de registros digital, onde qualquer alteração indevida pode ser detectada.

Objetivo do projeto

O sistema permite:

Registrar eventos de forma append-only (sem alterações posteriores)

Validar assinaturas digitais (Ed25519)

Garantir encadeamento criptográfico entre eventos

Manter a ordem correta dos acontecimentos

Oferecer base para auditoria confiável

Conceito principal

Cada evento contém:

Dados do evento (payload)

Hash do próprio evento

Hash do evento anterior (prevHash)

Assinatura digital do hash

Identidade de quem gerou o evento

Isso cria uma cadeia:

Evento 1 → hash1 Evento 2 → prevHash = hash1 → hash2 Evento 3 → prevHash = hash2 → hash3

Se qualquer evento antigo for alterado, os hashes seguintes deixam de ser válidos, quebrando a cadeia.

Tecnologias utilizadas

Java

Spring Boot

MySQL

JPA / Hibernate

SHA-256 (hash)

Ed25519 (assinaturas digitais)

BouncyCastle (suporte criptográfico)

Fluxo básico

O cliente cria um evento

Gera o hash do conteúdo

Assina o hash com sua chave privada

Envia o evento para o servidor

O servidor:

Verifica a assinatura

Recalcula e valida o hash

Verifica o encadeamento (prevHash)

Salva o evento

Atualiza o estado do stream

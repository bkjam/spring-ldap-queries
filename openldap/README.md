# LDAP Server

This is a mock LDAP server for retrieving user certificates

## Usage

```bash
# Start Ldap Server
docker-compose up -d

# Query Ldap Server
docker exec -it openldap /bin/bash
ldapsearch -x -H ldap://localhost -b "dc=example,dc=org" -D "cn=admin,dc=example,dc=org" -w admin 
```

## Generate userCertificate and Add to Ldap Entry

[boostrap.ldif] contains the mocked ldap data. The userCertificates are generated using the following commands:

```bash
# Generate an RSA Private Key for the Certificate Authority (password=password)
openssl genrsa -aes256 -out ca.key 4096

# Create Self-Signed Certificate for the Certificate Authority
openssl req -new -x509 -days 3650 -key ca.key -out ca.crt -extensions v3_ca

# Generate an RSA Private Key for the Personal E-Mail Certificate (password=password)
openssl genrsa -aes256 -out smime_test_user.key 4096

# Create the Certificate Signing Request
openssl req -new -key smime_test_user.key -out smime_test_user.csr

# Sign the Certificate Using the Certificate Authority
openssl x509 -req -days 3650 -in smime_test_user.csr -CA ca.crt -CAkey ca.key -set_serial 1 -out smime_test_user.crt -addtrust emailProtection -addreject clientAuth -addreject serverAuth -trustout -extfile ./smime.cnf -extensions smime

# Package the Certificate into the PKCS12 Format
openssl pkcs12 -export -in smime_test_user.crt -inkey smime_test_user.key -out smime_test_user.p12

# Convert PKCS12 Format to PEM
openssl pkcs12 -in smime_test_user.p12 -nodes -out smime_test_user.pem

# Convert PEM to DER
openssl x509 -outform DER -in smime_test_user.pem -out smime_test_user.der

# Create Ldif file using Ldif Tool
ldif -b "usercertificate;binary" < smime_test_user.der > smime_test_user.ldif

# Verification
openssl x509 -in smime_test_user.crt -purpose -noout -text
```

## References

- [https://betterprogramming.pub/ldap-docker-image-with-populated-users-3a5b4d090aa4](https://betterprogramming.pub/ldap-docker-image-with-populated-users-3a5b4d090aa4)
- [https://tldp.org/HOWTO/archived/LDAP-Implementation-HOWTO/certificates.html](https://tldp.org/HOWTO/archived/LDAP-Implementation-HOWTO/certificates.html)
- [https://devopscube.com/create-self-signed-certificates-openssl/](https://devopscube.com/create-self-signed-certificates-openssl/)
- [https://www.dalesandro.net/create-self-signed-smime-certificates/](https://www.dalesandro.net/create-self-signed-smime-certificates/)
- [https://gist.github.com/stevenhaddox/1501893](https://gist.github.com/stevenhaddox/1501893)


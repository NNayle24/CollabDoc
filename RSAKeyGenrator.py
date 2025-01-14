import argparse
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization

def RSAKeyGenerator(n, name):
    """
    Generate a pair of RSA keys with the specified key size
    """

    private_key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=n,
    )

    public_key = private_key.public_key()
    with open(f"/home/nnayle/v1/rezo/private_{name}", "wb") as private_file:
        private_file.write(private_key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.TraditionalOpenSSL,
            encryption_algorithm=serialization.NoEncryption()
        ))

    with open(f"/home/nnayle/v1/rezo/public_{name}", "wb") as public_file:
        public_file.write(public_key.public_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PublicFormat.SubjectPublicKeyInfo,
        ))

    return public_key, private_key

def main():
    parser = argparse.ArgumentParser(description="Generate RSA key pair")
    parser.add_argument("key_size", type=int, help="Size of the RSA key")
    parser.add_argument("name", type=str, help="Name to use for the key files")

    args = parser.parse_args()

    RSAKeyGenerator(args.key_size, args.name)

if __name__ == "__main__":
    main()

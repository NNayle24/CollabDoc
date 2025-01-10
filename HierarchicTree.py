import sys
import random as rd
import hashlib

def main():
    if len(sys.argv) < 2:
        print("Usage: python HierarchicTree.py <n> <FileDestination> [rootPassword]")
        sys.exit(1)

    try:
        n = int(sys.argv[1])
    except ValueError:
        print("The first parameter must be an integer.")
        sys.exit(1)

    file_destination = sys.argv[2]
    root_password = sys.argv[3] if len(sys.argv) > 3 else None

    if root_password is None:
        root_password = generate_RdString(16)
    if len(root_password) < 16:
        root_password = root_password + generate_RdString(16 - len(root_password))
    out=[]
    for i in range(n):
        out.append(root_password)
        root_password = hash_password(root_password+str(i))
    with open(file_destination, 'w') as f:
        f.write('\n'.join(out))



def generate_RdString(n):
    return ''.join(rd.choice("!#$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~") for i in range(n))

def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()[:16]

if __name__ == "__main__":
    main()

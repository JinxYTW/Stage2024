import hashlib

def jinx_hash(password):
    length = len(password)

    pos1=length//3
    pos2=2*length//3
    pos3=3*length//3

    modified_password = password[:pos1] +"get"+ password[pos1:pos2] +"jinxed"+ password[pos2:pos3] + "!"

    sha256_hash = hashlib.sha256(modified_password.encode("utf-8")).hexdigest()

    return sha256_hash

mdp = "password"
print(jinx_hash(mdp))
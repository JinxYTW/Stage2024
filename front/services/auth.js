function utilisateurAuthentifie() {
    // Exemple simplifié : Vérification si un JWT est présent dans le localStorage
    const jwtToken = localStorage.getItem('jwtToken');
    return jwtToken !== null; // Retourne vrai si l'utilisateur est authentifié, sinon faux
}

export {utilisateurAuthentifie};
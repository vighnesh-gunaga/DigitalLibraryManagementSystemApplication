if(requireLogin()){
document.getElementById("profileUsername").textContent=getUsername()||"—";
document.getElementById("profileEmail").textContent=getEmail()||"Not available";
document.getElementById("profileRole").textContent=getRole()||"USER";
document.getElementById("profileId").textContent=getUserId()||"—";
document.getElementById("profileToken").textContent=getToken()?"Authenticated":"Not authenticated";
}

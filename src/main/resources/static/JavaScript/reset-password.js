const form=document.getElementById("resetForm");
const msg=document.getElementById("message");
const token=new URLSearchParams(location.search).get("token");
if(!token && msg){msg.textContent="Reset token is missing from the URL.";msg.classList.remove("hidden");}
if(form){
  setupPasswordToggle("togglePassword","newPassword");
  setupPasswordToggle("toggleConfirm","confirmPassword");
  form.addEventListener("submit",async e=>{
    e.preventDefault();
    const p=document.getElementById("newPassword").value;
    const c=document.getElementById("confirmPassword").value;
    if(!token){showToast("Reset token is missing.","error");return;}
    if(p.length<6){showToast("Password must contain at least 6 characters.","error");return;}
    if(p!==c){showToast("Passwords do not match.","error");return;}
    const btn=document.getElementById("resetBtn");btn.disabled=true;btn.textContent="Resetting...";
    try{
      const r=await apiFetch("/auth/reset-password",{method:"POST",body:JSON.stringify({token,newPassword:p})});
      if(!r)return;
      const data=await readResponse(r);
      if(!r.ok){msg.textContent=typeof data==="string"?data:(data?.message||"Password reset failed.");msg.classList.remove("hidden");return;}
      showToast("Password reset successfully.","success");
      setTimeout(()=>location.href="/HTML/login.html",900);
    }catch(e){}finally{btn.disabled=false;btn.textContent="Reset Password";}
  });
}

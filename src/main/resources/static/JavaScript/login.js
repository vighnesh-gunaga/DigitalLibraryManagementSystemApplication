const form=document.getElementById("loginForm");

// Always start a fresh login session.
// This prevents an old/expired JWT from being reused.
clearAuth();
if(form){
  setupPasswordToggle("togglePassword","password");
  form.addEventListener("submit",async e=>{
    e.preventDefault();
    const btn=document.getElementById("loginBtn");
    const email=document.getElementById("email").value.trim();
    const password=document.getElementById("password").value;
    btn.disabled=true;btn.textContent="Signing in...";
    try{
      const r=await apiFetch("/auth/login",{method:"POST",body:JSON.stringify({email,password})});
      if(!r)return;
      const data=await readResponse(r);
      if(!r.ok){showToast(typeof data==="string"?data:(data?.message||"Invalid email or password"),"error");return;}
      saveAuth(data,email);
      showToast("Login successful.","success");
      setTimeout(()=>location.href=data.role==="ADMIN"?"/HTML/admin/admin-panel.html":"/HTML/dashboard.html",500);
    }catch(e){}finally{btn.disabled=false;btn.textContent="Login";}
  });
}

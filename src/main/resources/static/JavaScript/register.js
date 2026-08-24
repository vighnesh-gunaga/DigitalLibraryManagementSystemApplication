const form=document.getElementById("registerForm");
if(form){
  setupPasswordToggle("togglePassword","password");
  setupPasswordToggle("toggleConfirm","confirmPassword");
  form.addEventListener("submit",async e=>{
    e.preventDefault();
    const username=document.getElementById("username").value.trim();
    const email=document.getElementById("email").value.trim();
    const password=document.getElementById("password").value;
    const confirm=document.getElementById("confirmPassword").value;
    if(password!==confirm){showToast("Passwords do not match.","error");return;}
    if(password.length<6){showToast("Password must contain at least 6 characters.","error");return;}
    const btn=document.getElementById("registerBtn");btn.disabled=true;btn.textContent="Creating...";
    try{
      const r=await apiFetch("/auth/register",{method:"POST",body:JSON.stringify({username,email,password})});
      if(!r)return;
      const data=await readResponse(r);
      if(!r.ok){showToast(typeof data==="string"?data:(data?.message||"Registration failed"),"error");return;}
      if(String(data).toLowerCase().includes("already")){showToast(data,"error");return;}
      showToast("Registration successful. Please login.","success");
      setTimeout(()=>location.href="/HTML/login.html",700);
    }catch(e){}finally{btn.disabled=false;btn.textContent="Create Account";}
  });
}

const form=document.getElementById("forgotForm");
if(form){
  form.addEventListener("submit",async e=>{
    e.preventDefault();
    const btn=document.getElementById("forgotBtn"),msg=document.getElementById("message");
    const email=document.getElementById("email").value.trim();
    btn.disabled=true;btn.textContent="Generating...";
    try{
      const r=await apiFetch("/auth/forgot-password",{method:"POST",body:JSON.stringify({email})});
      if(!r)return;
      const data=await readResponse(r);
      msg.classList.remove("hidden");
      if(!r.ok){msg.textContent=typeof data==="string"?data:(data?.message||"Request failed.");return;}
      if(typeof data==="string" && data.startsWith("http")){
        msg.innerHTML=`Reset link generated:<br><a href="${escapeHtml(data)}">${escapeHtml(data)}</a>`;
      }else msg.textContent=data||"If the email exists, a reset link was generated.";
    }catch(e){msg.textContent="Cannot connect to the server.";}
    finally{btn.disabled=false;btn.textContent="Generate Reset Link";}
  });
}

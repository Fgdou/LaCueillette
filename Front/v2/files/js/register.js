$(()=>{
    $(".window.register .cancel").click(()=>{
        openWindow("login")
    })
    $(".window.register .register").submit(e=>{
        e.preventDefault()
    })
    $(".window.register .submit").click(()=> {

        clearInputError()

        let submit = $(".window.register .submit")

        let name = $(".window.register .name input").val()
        let surname = $(".window.register .surname input").val()
        let email = $(".window.register .email input").val()
        let tel = $(".window.register .tel input").val()
        let pass1 = $(".window.register .password input").val()
        let pass2 = $(".window.register .password2 input").val()

        if(name === "")
            inputError("name")
        else if(surname === "")
            inputError("surname")
        else if(email === "")
            inputError("email")
        else if(pass1 === "")
            inputError("pass1")
        else if(tel.length > 10){
            inputError("tel")
            errorPopup("Le téléphone ne doit pas excéder 10 chiffres")
        }
        else if(pass1 !== pass2)
            inputError("password2")
        else{
            submit.prop("disabled", true)

            $.post(api + "user/new", {
                name: name,
                surname: surname,
                tel: tel,
                email: email,
                password: pass1
            }, data=>{
                submit.prop("disabled", false)
                if(data.error){
                    if(data.error === "User already exist"){
                        inputError("email")
                        errorPopup("Cet email est déjà utilisé")
                    }else
                        errorPopup(data.error)
                }else{
                    successPopup("Utilisateur enregistré")
                    openWindow("login")
                    $(".window.login .email input").val(email)
                }
            }, "json")
        }

    })
})
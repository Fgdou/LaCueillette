$(()=>{
    $(".window.login .submit button").click(()=>{
        let bt = $(".window.login .submit")

        clearInputError()

        let email = $(".window.login .email input").val()
        let password = $(".window.login .password input").val()

        if(email === "")
            inputError("email")
        else if(password === "")
            inputError("password")
        else{
            bt.prop("disabled", true);

            $.post(api + "user/login", {
                email: email,
                password: password
            }, (data)=>{
                bt.prop("disabled", false);
                if(data.error){
                    if(data.error === "Wrong password")
                        inputError("password")
                    else if(data.error === "User not found")
                        inputError("email")
                    else
                        errorPopup(data.error)
                }else{
                    token = data.value
                    user = data.user
                    login(user)
                    openWindow(lastPage.pop())
                    document.cookie = "token="+data.value + "; expires="+dateToString(data.expiration)
                }
            },"json");
        }
    })
    $(".window.login form.login").submit(e => e.preventDefault())
    $(".window.login .register").click(()=>{
        openWindow("register")
    })

    let cookie = getCookie("token")
    if(cookie !== ""){
        $.post(api + "user/", {
            user_token: cookie
        }, (data)=>{
            if(data.error){
                errorPopup(data.error)
                document.cookie = "token=; expires=Thu, 1 Jan 1970 00:00:01 UTC"
            }else{
                user = data
                token = cookie
                login(user)
                setTimeout(()=>renewToken(),1000)
            }
        }, "json")
    }
})

function login(user){
    $("header .account span").text("Bonjour, " + user.name.charAt(0).toUpperCase() + user.name.slice(1))
    actCart()
}
function logout(){
    $.post(api+"user/logout", {
        user_token: token
    }, null, "json")
    $("header .account span").text("Mon compte")
    document.cookie = "token=; expires=Thu, 1 Jan 1970 00:00:01 UTC"
    user = null
    token = null
    openWindow("login")
    $("header .cart span").html("Panier (0)")
}

function getCookie(cookiename) {
    var name = cookiename + "=";

    // Decode URI to normal if needed and split into an array of cookies
    var arrayOfCookies = decodeURIComponent(document.cookie).split(';');

    // examine all elements in the cookie array (list)
    for (var i = 0; i < arrayOfCookies.length; i++) {
        // Get the next cookie from the array
        var aCookie = arrayOfCookies[i];
        // Skip prefix spaces
        while (aCookie.charAt(0) == ' ') {
            aCookie = aCookie.substring(1);
        }
        // if the cookie name is found, return the value part of the string
        if (aCookie.indexOf(name) == 0) {
            return aCookie.substring(name.length, aCookie.length);
        }
    }
    // return nothing if we didn't find the cookie name
    return "";
}
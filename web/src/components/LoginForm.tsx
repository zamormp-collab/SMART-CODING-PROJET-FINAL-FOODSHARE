import React from 'react';

const LoginForm: React.FC = () => {
    return (
        <form className="flex flex-col gap-4" onSubmit={(e) => e.preventDefault()}>
            <h2 className="text-brand-amber text-center font-bold text-xl uppercase mb-2">CONNEXION</h2>

            {/* email */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">EMAIL</label>
                <input
                    type="email"
                    placeholder=" "
                    className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                />
            </div>

            {/* Password */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">MOT DE PASSE</label>
                <input
                    type="password"
                    placeholder=" "
                    className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                />
            </div>

            <button className="mt-4 bg-brand-amber hover:bg-amber-500 text-brand-dark font-bold py-3 rounded-lg uppercase tracking-tighter transition-transform active:scale-95">
                SE CONNECTER
            </button>
        </form>
    );
};

export default LoginForm;
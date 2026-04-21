import { useNavigate } from "react-router-dom";

export default function DashboardKpi({ kpis }) {
    // Permite navegar para a listagem de orçamentos com filtro pré-aplicado
    const navigate = useNavigate();
    const total = (kpis.pendentes || 0) + (kpis.confirmados || 0) + (kpis.cancelados || 0);
    const calcPct = (value) => total > 0 ? Math.round((value / total) * 100) : 0;

    const cards = [
        {
            key: "pendentes",
            label: "Pendentes",
            value: kpis.pendentes || 0,
            sub: "Aguardando aprovação",
            // Valor exato do campo status no backend
            statusFilter: "em análise",
            accentClass: "kpi-accent-amber",
            iconBg: "#FAEEDA",
            barClass: "kpi-bar-amber",
            icon: (
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                    stroke="#BA7517" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="12 6 12 12 16 14" />
                </svg>
            ),
        },
        {
            key: "confirmados",
            label: "Confirmados",
            value: kpis.confirmados || 0,
            sub: "Prontos para execução",
            statusFilter: "confirmado",
            accentClass: "kpi-accent-purple",
            iconBg: "#EEEDFE",
            barClass: "kpi-bar-purple",
            icon: (
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                    stroke="#534AB7" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="20 6 9 17 4 12" />
                </svg>
            ),
        },
        {
            key: "cancelados",
            label: "Cancelados",
            value: kpis.cancelados || 0,
            sub: "Não realizados",
            statusFilter: "cancelado",
            accentClass: "kpi-accent-red",
            iconBg: "#FCEBEB",
            barClass: "kpi-bar-red",
            icon: (
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                    stroke="#A32D2D" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18" />
                    <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
            ),
        },
    ];

    return (
        <>
            <style>{`
                .kpi-grid {
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    gap: 14px;
                    width: 100%;
                    padding: 1rem 0;
                    box-sizing: border-box;
                }

                @media (max-width: 768px) {
                    .kpi-grid { gap: 10px; }
                }

                @media (max-width: 480px) {
                    .kpi-grid {
                        grid-template-columns: 1fr;
                        gap: 8px;
                        padding: 0.5rem 0;
                    }
                }

                .kpi-card {
                    background: #ffffff;
                    border: 0.5px solid #e5e5e5;
                    border-radius: 12px;
                    padding: 1rem 1.1rem 0.9rem;
                    position: relative;
                    overflow: hidden;
                    box-sizing: border-box;
                    transition: border-color 0.15s ease, box-shadow 0.15s ease;
                    /* Indica que o card é clicável */
                    cursor: pointer;
                }
                .kpi-card:hover {
                    border-color: #c9c9c9;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                }

                .kpi-card::before {
                    content: '';
                    position: absolute;
                    top: 0; left: 0; right: 0;
                    height: 3px;
                }
                .kpi-accent-amber::before  { background: #EF9F27; }
                .kpi-accent-purple::before { background: #7F77DD; }
                .kpi-accent-red::before    { background: #E24B4A; }

                @media (max-width: 480px) {
                    .kpi-card::before {
                        top: 0; bottom: 0; right: auto;
                        width: 3px; height: 100%;
                        border-radius: 10px 0 0 10px;
                    }
                }

                .kpi-top {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 8px;
                }
                .kpi-label {
                    font-size: 11px;
                    color: #888;
                    font-weight: 500;
                    letter-spacing: 0.06em;
                    text-transform: uppercase;
                }
                .kpi-icon {
                    width: 26px;
                    height: 26px;
                    border-radius: 6px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    flex-shrink: 0;
                }

                .kpi-number {
                    font-size: 30px;
                    font-weight: 500;
                    line-height: 1;
                    color: #1a1a1a;
                    margin-bottom: 4px;
                }
                .kpi-sub {
                    font-size: 12px;
                    color: #aaa;
                }

                .kpi-bar-section {
                    margin-top: 12px;
                }
                .kpi-bar-label {
                    display: flex;
                    justify-content: space-between;
                    font-size: 11px;
                    color: #bbb;
                    margin-bottom: 4px;
                }
                .kpi-bar-bg {
                    height: 3px;
                    background: #f0f0f0;
                    border-radius: 2px;
                    overflow: hidden;
                }
                .kpi-bar-fill {
                    height: 100%;
                    border-radius: 2px;
                    transition: width 0.6s ease;
                }
                .kpi-bar-amber  { background: #EF9F27; }
                .kpi-bar-purple { background: #7F77DD; }
                .kpi-bar-red    { background: #E24B4A; }

                @media (max-width: 480px) {
                    .kpi-card {
                        padding: 0.55rem 0.85rem 0.6rem 1rem;
                        border-radius: 10px;
                    }
                    .kpi-top {
                        margin-bottom: 0;
                    }
                    .kpi-body {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    .kpi-body-text { flex: 1; }
                    .kpi-number {
                        font-size: 20px;
                        margin-bottom: 0;
                    }
                    .kpi-sub { display: none; }
                    .kpi-bar-section { margin-top: 6px; }
                }
            `}</style>

            <div className="kpi-grid">
                {cards.map((card) => {
                    const pct = calcPct(card.value);
                    return (
                        <div
                            key={card.key}
                            className={`kpi-card ${card.accentClass}`}
                            // Navega para orçamentos passando o filtro de status via location.state
                            onClick={() => navigate("/orcamentos", { state: { statusFilter: card.statusFilter } })}
                        >
                            <div className="kpi-top">
                                <span className="kpi-label">{card.label}</span>
                                <div className="kpi-icon" style={{ background: card.iconBg }}>
                                    {card.icon}
                                </div>
                            </div>

                            <div className="kpi-body">
                                <div className="kpi-body-text">
                                    <div className="kpi-number">{card.value}</div>
                                    <div className="kpi-sub">{card.sub}</div>
                                </div>
                            </div>

                            <div className="kpi-bar-section">
                                <div className="kpi-bar-label">
                                    <span>do total</span>
                                    <span>{pct}%</span>
                                </div>
                                <div className="kpi-bar-bg">
                                    <div
                                        className={`kpi-bar-fill ${card.barClass}`}
                                        style={{ width: `${pct}%` }}
                                    />
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </>
    );
}
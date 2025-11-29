import React from "react";

export default function DashboardKpi() {
    return (
        <div className="w-full p-4 bg-purple-50">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="bg-white p-8 rounded-lg shadow">Lucro total $</div>
                <div className="bg-white p-8 rounded-lg shadow">Margem de lucro %</div>
                <div className="bg-white p-8 rounded-lg shadow">Lucro por cliente $ </div>
                <div className="bg-white p-8 rounded-lg shadow">Custo operacional total $</div>
            </div>
        </div>
    );
}